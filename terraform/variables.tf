variable "aws_profile" {
    default = "multicloud"
} 
 
variable "aws_region" {
    default = "us-east-1"
}

variable "image_id" {
    # Regular Amazon Linux
    default = "ami-9be6f38c"
}

variable "instance_type" {
    default = "t2.small"
}

variable "key_pair_name" {
    default = "ondemand-scaling-keypair"
}

variable "subnet_id" {
    default = "subnet-557b087d"
}

variable "security_group_id" {
    default = "sg-24647459"
}