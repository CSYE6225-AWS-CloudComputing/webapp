# Update system packages
sudo yum update -y

# Install PostgreSQL
sudo yum install -y postgresql-server postgresql-contrib

#Permission to alter postgres password
sudo chmod o+rx /home/admin

# Initialize the PostgreSQL database and start the service
sudo postgresql-setup initdb
sudo systemctl start postgresql
sudo systemctl enable postgresql

# Create a PostgreSQL user and database for your Spring Boot project
sudo -u postgres psql -c "ALTER USER postgres with password 'root';"
sudo -u postgres psql -c "CREATE DATABASE webapp WITH OWNER = postgres;"

# Grant necessary privileges to the user
sudo -u postgres psql -c "ALTER USER postgres WITH SUPERUSER;"

#restart postgres service
sudo systemctl restart postgresql
