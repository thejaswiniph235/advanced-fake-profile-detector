package com.fakeprofile.service;

import com.fakeprofile.model.*;
import com.fakeprofile.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DetectionService {
    private final DetectionRepository detections;
    private final UserRepository users;
    private final RestTemplate http = new RestTemplate();
    @Value("${app.ml.url}") private String mlUrl;

    public DetectionService(DetectionRepository d, UserRepository u){detections=d;users=u;}

    public Map<String,Object> analyze(String email, Map<String,Object> f) {
        User user=users.findByEmail(email).orElseThrow();

        int followers=n(f,"followers"), following=n(f,"following"), posts=n(f,"posts"),
            age=n(f,"accountAgeDays"), ppd=n(f,"postsPerDay");
        boolean pic=b(f,"hasProfilePicture"), bio=b(f,"hasBio"),
            repeat=b(f,"repeatedComments"), activity=b(f,"twentyFourHourActivity");

        List<String> reasons=new ArrayList<>();
        double rule=0;
        if(age<30){rule+=20; reasons.add("Very new account");}
        if(following > Math.max(10,followers*5)){rule+=20; reasons.add("Unusual follower/following ratio");}
        if(ppd>30){rule+=20; reasons.add("Very high posting frequency");}
        if(!pic){rule+=10; reasons.add("No profile picture");}
        if(!bio){rule+=10; reasons.add("No profile bio");}
        if(repeat){rule+=10; reasons.add("Repeated comments detected");}
        if(activity){rule+=10; reasons.add("Continuous activity pattern");}
        rule=Math.min(100,rule);

        double ml=rule/100.0;
        try {
            Map<String,Object> body=new HashMap<>();
            body.put("followers",followers); body.put("following",following);
            body.put("posts",posts); body.put("accountAgeDays",age);
            body.put("postsPerDay",ppd); body.put("hasProfilePicture",pic);
            body.put("hasBio",bio); body.put("repeatedComments",repeat);
            body.put("twentyFourHourActivity",activity);
            ResponseEntity<Map> response=http.postForEntity(mlUrl+"/predict",body,Map.class);
            if(response.getBody()!=null && response.getBody().get("botProbability")!=null)
                ml=((Number)response.getBody().get("botProbability")).doubleValue();
        } catch(Exception ignored) {}

        double risk=Math.round(((rule*0.4)+(ml*100*0.6))*10.0)/10.0;
        String prediction=risk>=70?"BOT":risk>=40?"SUSPICIOUS":"GENUINE";

        Detection d=new Detection();
        d.setUser(user); d.setUsername(String.valueOf(f.getOrDefault("username","unknown")));
        d.setFollowers(followers); d.setFollowing(following); d.setPosts(posts);
        d.setAccountAgeDays(age); d.setPostsPerDay(ppd); d.setHasProfilePicture(pic);
        d.setHasBio(bio); d.setRepeatedComments(repeat); d.setTwentyFourHourActivity(activity);
        d.setMlProbability(ml); d.setRuleScore(rule); d.setRiskScore(risk);
        d.setPrediction(prediction); d.setReasons(String.join("|",reasons));
        d=detections.save(d);

        return result(d);
    }

    public List<Map<String,Object>> history(String email){
        User u=users.findByEmail(email).orElseThrow();
        return detections.findTop50ByUserIdOrderByCreatedAtDesc(u.getId()).stream().map(this::result).toList();
    }

    public Map<String,Object> stats(String email){
        User u=users.findByEmail(email).orElseThrow();
        long total=detections.countByUserId(u.getId());
        long bots=detections.countByUserIdAndPrediction(u.getId(),"BOT");
        long suspicious=detections.countByUserIdAndPrediction(u.getId(),"SUSPICIOUS");
        return Map.of("total",total,"bots",bots,"suspicious",suspicious,"genuine",Math.max(0,total-bots-suspicious));
    }

    private Map<String,Object> result(Detection d){
        return Map.of("id",d.getId(),"username",d.getUsername(),"riskScore",d.getRiskScore(),
                "mlProbability",Math.round(d.getMlProbability()*1000.0)/10.0,
                "ruleScore",d.getRuleScore(),"prediction",d.getPrediction(),
                "reasons",d.getReasons().isBlank()?List.of():Arrays.asList(d.getReasons().split("\\|")),
                "createdAt",d.getCreatedAt().toString());
    }
    private int n(Map<String,Object> m,String k){return ((Number)m.getOrDefault(k,0)).intValue();}
    private boolean b(Map<String,Object> m,String k){return Boolean.TRUE.equals(m.get(k));}
}
