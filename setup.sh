aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 913524932573.dkr.ecr.us-east-1.amazonaws.com
aws ecr create-repository --repository-name fiap-hackaton-aws

docker buildx build --platform linux/amd64 --provenance=false -t docker-image:test .
docker tag docker-image:test  913524932573.dkr.ecr.us-east-1.amazonaws.com/presigned-lambda-repo:latest
docker push 913524932573.dkr.ecr.us-east-1.amazonaws.com/presigned-lambda-repo:latest

#$assumeRolePolicy = @'
#{
#    "Version": "2012-10-17",
#    "Statement": [
#        {
#            "Effect": "Allow",
#            "Principal": { "Service": "lambda.amazonaws.com" },
#            "Action": "sts:AssumeRole"
#        }
#    ]
#}
#'@
#
#aws iam create-role --role-name LambdaS3UploadRole --assume-role-policy-document $assumeRolePolicy
#
#
#$policyDocument = @'
#{
#    "Version": "2012-10-17",
#    "Statement": [
#        {
#            "Effect": "Allow",
#            "Action": "s3:PutObject",
#            "Resource": "arn:aws:s3:::bucket-fiap-hackaton/*"
#        }
#    ]
#}
#'@
#
#aws iam create-role  --role-name LambdaS3UploadRole --policy-name S3PutObjectPolicy --policy-document $policyDocument
#
#
#
#aws lambda create-function \
#    --function-name presigned-url \
#    --package-type Image \
#    --code ImageUri=913524932573.dkr.ecr.us-east-1.amazonaws.com/fiap-hackaton-aws:2025133.1 \
#    --role arn:aws:iam::913524932573:role/LambdaS3UploadRole \
#    --memory-size 512 \
#    --timeout 30