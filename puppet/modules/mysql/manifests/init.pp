class mysql {
   package {
      "mysql-client":
      ensure => present
   }

   package {
      "mysql-server":
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

   "chains_db":
      unless => "mysql -uroot -proot chains",
      command => "mysql -uroot -proot -e 'create database chains'",
      require => Exec["mysql_password"]
   }
}

