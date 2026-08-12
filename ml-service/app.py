from fastapi import FastAPI
from pydantic import BaseModel
from sklearn.ensemble import RandomForestClassifier
import numpy as np
import random

app = FastAPI(title="Fake Profile ML Service")

FEATURES = [
    "followers","following","posts","accountAgeDays","postsPerDay",
    "hasProfilePicture","hasBio","repeatedComments","twentyFourHourActivity"
]

class Profile(BaseModel):
    followers: int = 0
    following: int = 0
    posts: int = 0
    accountAgeDays: int = 0
    postsPerDay: int = 0
    hasProfilePicture: bool = True
    hasBio: bool = True
    repeatedComments: bool = False
    twentyFourHourActivity: bool = False

def make_dataset(n=5000):
    X, y = [], []
    for _ in range(n):
        followers=random.randint(0,10000)
        following=random.randint(0,12000)
        posts=random.randint(0,10000)
        age=random.randint(1,2500)
        ppd=random.randint(0,150)
        pic=random.randint(0,1)
        bio=random.randint(0,1)
        repeat=random.randint(0,1)
        active=random.randint(0,1)

        suspicious = (
            age < 30 and following > max(50, followers*3)
            or ppd > 40
            or repeat and active
            or (not pic and not bio and age < 90)
            or following > 5000 and followers < 500
        )
        noise = random.random() < 0.08
        label = int(suspicious != noise)

        X.append([followers,following,posts,age,ppd,pic,bio,repeat,active])
        y.append(label)
    return np.array(X), np.array(y)

X, y = make_dataset()
model = RandomForestClassifier(n_estimators=180, max_depth=12, random_state=42, class_weight="balanced")
model.fit(X, y)

@app.get("/health")
def health():
    return {"status":"ok","model":"RandomForest"}

@app.post("/predict")
def predict(p: Profile):
    x=np.array([[getattr(p,k) for k in FEATURES]], dtype=float)
    probability=float(model.predict_proba(x)[0][1])
    return {
        "botProbability": probability,
        "prediction": "BOT" if probability >= 0.7 else "SUSPICIOUS" if probability >= 0.4 else "GENUINE"
    }
