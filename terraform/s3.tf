resource "aws_s3_bucket" "ic-ondemand-scaling" {
    bucket = "ic-ondeman-scaling"
    acl = "private"

    tags {
        Name = "My bucket"
        Environment = "Dev"
    }
}
