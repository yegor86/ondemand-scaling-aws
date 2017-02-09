## OnDemand Scaling with AWS

This project aims to show the power of AWS Simple Workflow (SWF) framework and the scope of the tasks which can be covered by it.
It essentially follows Microservice architecture where each building block represents an independent deployable module and communicates through 
a well-defined mechanism to serve a business goal.

### Deployment guide

    $ git clone https://github.com/yegor86/ondemand-scaling-aws.git
    $ cd ondemand-scaling-aws/agent
    $ mvn clean package
    $ cd ..
    $ cd ondemand-scaling-aws/workflow-starter
    $ mvn clean package
    $ cd ..
    $ cd terraform
    $ terraform remote config -backend=s3 -backend-config="bucket=ic-terraform-state" -backend-config="key=terraform.tfstate" -backend-config="region=us-east-1"
    $ terraform get
    $ terraform plan
    $ terraform apply

