## OnDemand Scaling

### Sync up Terraform state

    $ terraform remote config -backend=s3 -backend-config="bucket=ic-terraform-state" -backend-config="key=terraform.tfstate" -backend-config="region=us-east-1"

