dataSource {
    pooled = true
    driverClassName = "org.h2.Driver"
    username = "sa"
    password = ""
}
hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = false
    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory' // Hibernate 3
//    cache.region.factory_class = 'org.hibernate.cache.ehcache.EhCacheRegionFactory' // Hibernate 4
}

// environment specific settings
environments {
    development {
        dataSource {
            dbCreate = "create-drop" // one of 'create', 'create-drop', 'update', 'validate', ''
            url = "jdbc:h2:mem:greenlight2;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE"

//            pooled = true
//            dbCreate = "update"
//            url = "jdbc:postgresql://localhost:5432/greenlight"
//            driverClassName = "org.postgresql.Driver"
//            dialect = net.sf.hibernate.dialect.PostgreSQLDialect
//            username = "postgres"
//            password = "1"

        }
    }
    test {
        dataSource {
            dbCreate = "update"
            url = "jdbc:h2:mem:testDb;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE"
        }
    }
    production {
        dataSource {
            pooled = true
            dbCreate = "update"
            url = "jdbc:postgresql://localhost:5432/greenlight"
            driverClassName = "org.postgresql.Driver"
            dialect = net.sf.hibernate.dialect.PostgreSQLDialect


//            dbCreate = "update"
//            url = "jdbc:h2:prodDb;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE"
//            properties {
//               maxActive = -1
//               minEvictableIdleTimeMillis=1800000
//               timeBetweenEvictionRunsMillis=1800000
//               numTestsPerEvictionRun=3
//               testOnBorrow=true
//               testWhileIdle=true
//               testOnReturn=false
//               validationQuery="SELECT 1"
//               jdbcInterceptors="ConnectionState"
 //           }
        }
    }
}
