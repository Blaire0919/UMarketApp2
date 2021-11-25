import pyrebase
import pandas as pd
import json
from pandas.io.json import json_normalize
from os.path import dirname, join
import warnings
from sklearn.metrics.pairwise import linear_kernel
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.preprocessing import FunctionTransformer

warnings.filterwarnings('ignore')

## import warnings
## warnings.filterwarnings('ignore')

#filename = join(dirname(_file_), "umarketapp.json")
#now open file

#with open(filename, 'r', encoding="utf8", errors="ignore") as f:
#    d = f.read()



def main(itemName):
    firebaseConfig = {
        "apiKey": "AIzaSyAWdKlV_6uha2Cq3vPlSNeRm5OD7ZiRy4Y",
        "authDomain": "umarketapp-c3ff2.firebaseapp.com",
        "databaseURL": "https://umarketapp-c3ff2-default-rtdb.asia-southeast1.firebasedatabase.app",
        "projectId": "umarketapp-c3ff2",
        "storageBucket": "umarketapp-c3ff2.appspot.com",
        "messagingSenderId": "735215233789",
        "appId": "1:735215233789:web:eda19d1df295577728ec94",
        "measurementId": "G-5EX4YS54RC"
    }

    firebase = pyrebase.initialize_app(firebaseConfig)

    # Set Database
    db = firebase.database()

    #  Retrieve Data
    prod = db.child("products").get()

    # Products into Dataframe
    df = pd.DataFrame(prod.val())

    # reverse Column and rows
    df_t = df.T
    # Reset Index
    df_t = df_t.reset_index(drop=True)
    df_t

    #Define a TF-IDF Vectorizer Object. Remove all english stop words such as 'the', 'a'
    tfidf = TfidfVectorizer(stop_words='english')

    tf_train_data = pd.concat([df_t['pCategory'], df_t['pDescription']])

    #Construct the required TF-IDF matrix by fitting and transforming the data
    tfidf_matrix = tfidf.fit_transform(tf_train_data)

    #Output the shape of tfidf_matrix
    tfidf_matrix.shape

    # Compute the cosine similarity matrix
    cosine_sim = linear_kernel(tfidf_matrix, tfidf_matrix)

    #Construct a reverse map of indices and product titles
    indices = pd.Series(df_t.index, index=df_t['pName']).drop_duplicates()

    # Function that takes in movie title as input and outputs most similar movies
    def get_recommendations(title, cosine_sim=cosine_sim):
        # Get the index of the product that matches the title
        idx = indices[title]

        # Get the pairwsie similarity scores of all products with that product
        sim_scores = list(enumerate(cosine_sim[idx]))

        # Sort the product based on the similarity scores
        sim_scores = sorted(sim_scores, key=lambda x: x[1], reverse=True)

        # Get the scores of the 10 most similar movies
        sim_scores = sim_scores[0:11]

        # Get the product indices
        prod_indices = [i[0] for i in sim_scores]

        # Return the top 10 most similar product
        #  return df_t['pName'].iloc[prod_indices]
        return df_t['pName'].loc[prod_indices]


    recommends = []
    for i in get_recommendations(itemName):
        if i != itemName:
            recommends.append(i)

    # print(recommends)

    return recommends