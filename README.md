# FortuneCookie-Classifier
A Naive Bayes Classifier that classifies Fortune Cookie messages into 0. Wise Quotes and 1. Predictions

# Compiling and Running
1. Install [scala](https://www.scala-lang.org/) in your machine
- Usage: `FortuneClassifier <stoplist> <traindata> <trainlabels> <testdata> <testlabels>`
- Method 1: Compiling and Running
2. run: `scalac -d bin src/*`
3. run: `scala -cp ./bin FortuneClassifier "./data/stoplist.txt" "./data/training/traindata.txt" "./data/training/trainlabels.txt" "./data/test/testdata.txt" "./data/test/testlabels.txt"`
- Method 2: Running straight (Slower)
2. run: `scala src/FortuneClassifier.scala "./data/stoplist.txt" "./data/training/traindata.txt" "./data/training/trainlabels.txt" "./data/test/testdata.txt" "./data/test/testlabels.txt"`

