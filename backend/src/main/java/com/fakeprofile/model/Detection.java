package com.fakeprofile.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="detections")
public class Detection {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false, fetch=FetchType.LAZY)
    private User user;

    private String username;
    private int followers;
    private int following;
    private int posts;
    private int accountAgeDays;
    private int postsPerDay;
    private boolean hasProfilePicture;
    private boolean hasBio;
    private boolean repeatedComments;
    private boolean twentyFourHourActivity;

    private double mlProbability;
    private double ruleScore;
    private double riskScore;

    @Column(nullable=false)
    private String prediction;

    @Column(length=4000)
    private String reasons;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Detection() {}
    public Long getId(){return id;}
    public User getUser(){return user;}
    public void setUser(User user){this.user=user;}
    public String getUsername(){return username;}
    public void setUsername(String v){username=v;}
    public int getFollowers(){return followers;}
    public void setFollowers(int v){followers=v;}
    public int getFollowing(){return following;}
    public void setFollowing(int v){following=v;}
    public int getPosts(){return posts;}
    public void setPosts(int v){posts=v;}
    public int getAccountAgeDays(){return accountAgeDays;}
    public void setAccountAgeDays(int v){accountAgeDays=v;}
    public int getPostsPerDay(){return postsPerDay;}
    public void setPostsPerDay(int v){postsPerDay=v;}
    public boolean isHasProfilePicture(){return hasProfilePicture;}
    public void setHasProfilePicture(boolean v){hasProfilePicture=v;}
    public boolean isHasBio(){return hasBio;}
    public void setHasBio(boolean v){hasBio=v;}
    public boolean isRepeatedComments(){return repeatedComments;}
    public void setRepeatedComments(boolean v){repeatedComments=v;}
    public boolean isTwentyFourHourActivity(){return twentyFourHourActivity;}
    public void setTwentyFourHourActivity(boolean v){twentyFourHourActivity=v;}
    public double getMlProbability(){return mlProbability;}
    public void setMlProbability(double v){mlProbability=v;}
    public double getRuleScore(){return ruleScore;}
    public void setRuleScore(double v){ruleScore=v;}
    public double getRiskScore(){return riskScore;}
    public void setRiskScore(double v){riskScore=v;}
    public String getPrediction(){return prediction;}
    public void setPrediction(String v){prediction=v;}
    public String getReasons(){return reasons;}
    public void setReasons(String v){reasons=v;}
    public LocalDateTime getCreatedAt(){return createdAt;}
}
