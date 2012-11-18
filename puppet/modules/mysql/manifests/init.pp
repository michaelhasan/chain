class mysql {
   package {
      "mysql-client":
      ensure => present
   }

   package {
      "mysql":
      ensure => installed,
      before => File["/etc/my.cnf"]
   }

   file {
      "/etc/my.cnf":
      owner => root,
      group => root,
      mode => 644,
      source => "puppet:///modules/mysql/my.cnf"
   }  

   service {
      "mysql":
      ensure => running,
      subscribe => File["/etc/my.cnf"]
   }

   exec {
      "mysql_password":
      unless => "mysqladmin -uroot -proot status",
      command => "mysqladmin -uroot password root",
      require => Service[mysql];

   "massiveapp_db":
      unless => "mysql -uroot -proot massiveapp_production",
      command => "mysql -uroot -proot -e 'create database massiveapp_production'",
      require => Exec["mysql_password"]
   }
}

