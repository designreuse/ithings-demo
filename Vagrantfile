# -*- mode: ruby -*-
# vi: set ft=ruby :

# Base hostname
cookbook = 'ithings'

# vagrant up BOX # BOX being centos6, ubuntu1404

# Vagrantfile API/syntax version. Don't touch unless you know what you're doing!
VAGRANTFILE_API_VERSION = "2"

# AWS region
if ENV['AWS_REGION']
  AWS_REGION = ENV['AWS_REGION']
elsif ENV['AWS_DEFAULT_REGION']
  AWS_REGION = ENV['AWS_DEFAULT_REGION']
elsif ENV['EC2_URL']
  AWS_REGION = ENV['EC2_URL'].gsub(/^http(s)?:\/\/ec2\./, '').gsub(/.amazonaws.com$/, '')
else
  AWS_REGION = 'eu-west-1'
end

# SSH information
SSH_KEYPAIR = ENV['AWS_SSH_KEYPAIR']
SSH_PRIVATE_KEY_PATH = ENV['AWS_SSH_PRIVATE_KEY_PATH']
SSH_PUBLIC_KEY_PATH = ENV['AWS_SSH_PUBLIC_KEY_PATH']

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|
  
  # Default:
  ############- VirtualBox Provider -###########
  # add to /etc/hosts : 192.168.33.22	docker-kitchen
  config.vm.network :private_network, ip: "192.168.22.22"
  # forward docker port to the host (add to .bash_profile: export DOCKER_HOST=tcp://localhost:4243 )
  #config.vm.network "forwarded_port", guest: 4243, host: 4243
  # -> update me here accordingly <-
  config.vm.synced_folder "/Users/tph/Documents/BrownBagThursday/iThings-Demo", "/usr/var/rsd", create: true #, owner: "vagrant", group: "vagrant"
  
  # $ vagrant plugin install vagrant-omnibus
  config.omnibus.chef_version = :latest
  # $ vagrant plugin install vagrant-berkshelf --plugin-version '>= 2.0.1'
  config.berkshelf.enabled = false
  # $ vagrant plugin install vagrant-cachier
  config.cache.auto_detect = true
  
  #if Vagrant.has_plugin?("vagrant-proxyconf")
  #  config.proxy.http     = "http://10.10.32.132:3128"
  #  config.proxy.https    = "http://10.10.32.132:3128"
  #  config.apt_proxy.http = "http://10.10.32.132:3128"
  #  config.apt_proxy.https= "http://10.10.32.132:3128"
  #  config.apt_proxy.ftp  = "http://10.10.32.132:3128"
  #  config.yum_proxy.http = "http://10.10.32.132:3128"
  #  config.proxy.no_proxy = "localhost,127.0.0.1"
  #end
  
  # Default configs for VirtualBox
  config.vm.provider :virtualbox do |vb|
    #vb.gui = true
    vb.memory = 5120
    vb.cpus = 3
    vb.customize ["modifyvm", :id, "--natdnshostresolver1", "on"]
    vb.customize ["modifyvm", :id, "--natdnsproxy1", "on"]
    config.vm.box = 'opscode-ubuntu-14.04'
    config.vm.box_url = 'http://opscode-vm-bento.s3.amazonaws.com/vagrant/virtualbox/opscode_ubuntu-14.04_chef-provisionerless.box'
  end
  
  config.vm.provider 'aws' do |aws, override|
    aws.region = AWS_REGION
    aws.keypair_name = SSH_KEYPAIR
    aws.instance_type = 't1.micro'
    aws.security_groups = 'default'
    override.vm.box = 'dummy'
    override.vm.box_url = 'https://github.com/mitchellh/vagrant-aws/raw/master/dummy.box'
    override.ssh.private_key_path = SSH_PRIVATE_KEY_PATH
  end
  
  
  config.vm.define :ubuntu do |ubuntu1404|
    ubuntu1404.vm.hostname = "#{cookbook}-ubuntu-1404"
    ubuntu1404.vm.provider 'aws' do |aws, override|
      aws.ami = ''
      override.ssh.username = 'ubuntu' # adapt depending AMI
    end
    ubuntu1404.vm.provider 'virtualbox' do |vb, override|
      vb.name = "#{cookbook}-ubuntu1404"
      override.ssh.username = 'vagrant'
    end
    #config.vm.provision "shell", inline: "sudo yum -y update"
  end
  
  #config.vm.provision "shell", inline: "sudo apt-get update && sudo apt-get install -y ca-certificates"
 
  config.vm.provision :chef_solo do |chef|
    # berks vendor ./cookbooks
    chef.cookbooks_path = "cookbooks"
    chef.log_level = :debug
    chef.run_list = ['recipe[java]'] #'recipe[jenkins::master]', 'recipe[packer]'
    chef.json = {
        "java" => {
          "install_flavor" => "oracle",
          "jdk_version" => "8",
          "oracle" => {
            "accept_oracle_download_terms" => true
          }
        }
      }
  end
  
  
end