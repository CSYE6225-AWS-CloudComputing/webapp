#add group
sudo groupadd csye6225

#add user to group
sudo useradd -s /usr/sbin/nologin -g csye6225 csye6225

pwd
# shellcheck disable=SC2164
cd ../csye6225/webapp/
pwd
sudo cp ./startup-scripts/webapp.service /etc/systemd/system
sudo cp ./startup-scripts/config.yaml /etc/google-cloud-ops-agent
# shellcheck disable=SC2164
cd ../../../var/log
pwd
sudo mkdir webapp
sudo chmod u+w /var/log/webapp
sudo chown -R csye6225:csye6225 /home/csye6225/webapp
sudo chmod 744 /home/csye6225/webapp

sudo systemctl daemon-reload
sudo systemctl enable webapp
sudo systemctl start webapp
