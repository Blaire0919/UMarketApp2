import pyrebase
import pandas as pd
import warnings
from sklearn.metrics.pairwise import linear_kernel, cosine_similarity
from sklearn.feature_extraction.text import TfidfVectorizer, CountVectorizer

warnings.filterwarnings('ignore')

def main(studID):
    firebaseConfig = {
        "apiKey": "AIzaSyAE9LoONOQgkiWsAEAvnVqCSrQGUJkezIo",
        "authDomain": "umarketapp2-58178.firebaseapp.com",
        "databaseURL": "https://umarketapp2-58178-default-rtdb.asia-southeast1.firebasedatabase.app",
        "projectId": "umarketapp2-58178",
        "storageBucket": "umarketapp2-58178.appspot.com",
        "messagingSenderId": "740426938880",
        "appId": "1:740426938880:web:d2e577322eb1eb7a9c36e5",
        "measurementId": "G-50NH6QW3PX"
    }

    firebase = pyrebase.initialize_app(firebaseConfig)

    # Set Database
    db = firebase.database()

    # Products
    #  Retrieve Data
    prod = db.child("products").get()

    # Products into Dataframe
    df = pd.DataFrame(prod.val())

    # reverse Column and rows
    df_t = df.T
    # Reset Index
    df_t = df_t.reset_index(drop=True)

    # User Interest
    #  Retrieve Data
    uint = db.child("interests").get()

    # Products into Dataframe
    dfInt = pd.DataFrame(uint.val())

    # reverse Column and rows
    dfInt_t = dfInt.T

    # Reset Index
    dfInt_t = dfInt_t.reset_index(drop=True)

    # Lowercase
    def cleanData(x):
        if isinstance(x, list):
            return str.lower(x)
        else:
            if isinstance(x, str):
                return str.lower(x)
            else:
                return ''

    # Declare the Feature that would be used in content base filtering
    features = ['pName', 'pCategory', 'pDescription', 'pSubCategory']

    # Cleaning each Feature and assigning them their own column
    for feature in features:
        df_t[feature + '1'] = df_t[feature].apply(cleanData)

    # Combine Function for product
    def combine(x):
        # new columns for algo application and to prevent affecting the original data
        return x['pName1'] + ' ' + x['pCategory1'] + ' ' + x['pDescription1'] + ' ' + x['pSubCategory1']

    # Apply the function combine
    df_t['merged'] = df_t.apply(combine, axis=1)
    # delete the new columns as processing is done on the merged column
    df_t.drop(columns=['pName1', 'pCategory1', 'pDescription1', 'pSubCategory1'], inplace=True)

    # Declare the interest that would be used in cosine similarity
    interests = ['beautyproducts', 'clothesaccessories', 'electronics', 'foodbeverages', 'schoolsupplies', 'sportsequipment']

    # Cleaning each interests and assigning them their own column
    for interest in interests:
        dfInt_t[interest + '1'] = dfInt_t[interest].apply(cleanData)

    def merge(x):
        # new columns for algo application and to prevent affecting the original data
        return x['beautyproducts1'] + ' ' + x['clothesaccessories1'] + ' ' + x['electronics1'] + ' ' + x['foodbeverages1']+ ' ' + x['schoolsupplies1']+ ' ' + x['sportsequipment1']

    # Apply the function combine
    dfInt_t['merged'] = dfInt_t.apply(merge, axis=1)

    # get interest based on student id
    userint = pd.DataFrame()
    interest = dfInt_t.loc[dfInt_t['studid'] == studID]
    userint['source'] = interest['studid']
    userint['merged'] = interest['merged']

    # temproray dataframe holding pname and merged features
    prod_features = pd.DataFrame()
    prod_features['source'] = df_t['pName']
    prod_features['merged'] = df_t['merged']

    # combine the prod_feature and user interest
    all_feature = pd.DataFrame()
    all_feature = pd.concat([prod_features, userint], ignore_index = True, axis = 0)

    tf = TfidfVectorizer(analyzer='word', ngram_range=(1, 2), min_df=0, stop_words='english')
    tf_matrix = tf.fit_transform(all_feature['merged'])
    csmint_tf = linear_kernel(tf_matrix, tf_matrix)

    count = CountVectorizer(stop_words='english')
    count_matrixInt = count.fit_transform(all_feature['merged'])
    csmint_count = cosine_similarity(count_matrixInt, count_matrixInt)

    indices = pd.Series(all_feature.index, index=all_feature.source)

    prod_indices = []
    prod_similarity = []

    def classify(name, csm=(csmint_tf + csmint_count)/2):        # choosing this csm as it covers both aspects
        idx = indices[name]
        sim_series = list(enumerate(csm[idx]))
        sim_series = sorted(sim_series, key=lambda x: x[1], reverse=True)
        sim_series = sim_series[1:]   # not recommending the original post itself, starting from 1
    #     prod_indices = [i[0] for i in sim_series]

        for i in sim_series:
            if i[1] > 0.10:
                prod_indices.append(i[0])
            if i[1] > 0.10:
                prod_similarity.append(i)
    # Return the top 10 most similar product
    #     return df_t['pName'].iloc[prod_indices]

    # return with products and ranked by the similarity score
        ints = {'Recommendation': all_feature['source'].iloc[prod_indices], 'Similarity Score': prod_similarity}
        cosint = pd.DataFrame(ints)

        return cosint

    ret = pd.DataFrame()
    ret = classify(studID)

    products = ret['Recommendation']
    prod_list = []
    for product in products:
        temp = df_t.loc[df_t['pName'] == product]
        score = float(temp['pScore'])
        if score > 0:
            prod_list.append(product)

    return prod_list


