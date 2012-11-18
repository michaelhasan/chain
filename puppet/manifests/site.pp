Exec {
   path => "/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"
}

node "db" {
   include mysql
}
