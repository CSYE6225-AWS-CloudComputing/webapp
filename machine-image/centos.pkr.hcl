packer {
  required_plugins {
    googlecompute = {
      source  = "github.com/hashicorp/googlecompute"
      version = "~> 1"
    }
  }
}

variable "project_id" {
  type    = string
  default = "csye6225-dev-414521"
}

variable "ssh_username" {
  type    = string
  default = "admin"
}

variable "script_paths" {
  default = [
    "../startup-scripts/install-db.sh",
    "../startup-scripts/install-java-maven-tomcat.sh"
  ]
}

variable "image_description" {
  default ="Custom image for springboot application"
}

variable "base_image" {
  default="centos-stream-8-v20240110"
}

variable zone{
  default="us-central1-a"
}

source "googlecompute" "custom_image" {
  project_id       = var.project_id
  source_image     = var.base_image
  image_name       = "centos-{{timestamp}}"
  image_description = var.custom_image
  image_labels     = {
    created_by   = "packer"
    environment  = "dev"
  }
  zone             = var.zone
  ssh_username     = var.ssh_username
  use_internal_ip  = false
}

build {
  sources = ["source.googlecompute.custom_image"]

  provisioner "shell" {
    scripts = var.script_paths
  }

  provisioner "shell" {
    inline = [
      "sudo yum install -y unzip"
    ]
  }

  provisioner "file" {
    source = "../target/webapp-0.0.1-SNAPSHOT.jar"
    destination = "home/admin"
  }
}
