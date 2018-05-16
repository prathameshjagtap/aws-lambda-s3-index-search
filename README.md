# aws-lambda-s3-index-search
This will allow you to maintain your Lucene index in S3 and run your Lucene search across S3 bucket.

The idea here is to make complete search architecture serverless by keeping the Lucene index in S3 which provides us with unlimited cheap storage and leveraging AWS Lambda serverless platform to also keep the request cheap and scalable.

Though it is not truly scalable yet, as first time initialization of IndexReader takes time to load. Hence to keep reader hot by keeping its initialization in Constructor. This makes the first run slower but all the subsequent runs can attain ms. 

As lucene interfaces with files over java nio2, this implementation is made possible using com.upplication.s3fs package which provides nio2 implementation on the top of a S3 file system. 

Future State
- Indexing directly to S3 making both way serverless: We are unable to do it yet as S3 do not allow Atomic Move of files that is required by Lucene.
- Keeping the IndexReader object somewhere in cache to allow fast load on first run




