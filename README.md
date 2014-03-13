Greenlight 
=======
[![Build Status](https://travis-ci.org/OxBRCInformatics/greenlight.png?branch=develop)](https://travis-ci.org/OxBRCInformatics/greenlight)

Open Source consent management tool
------------
This project is built in Grails and uses GSP in most sections.
A number of libraries such as JQuery, Bootstrap,... are also used.



## Deploying the application
Current production version uses PostgreSQL. 
To run the deployed war file, you need to configure the 'greenlight-config.groovy' file, save it in 'userHome/.grails/' and set up your credentials in this file to access the target PostgreSQL database. The contents of the file should be something like:

```
dataSource {
    url = "jdbc:postgresql://localhost:5432/greenlight"
    username = "greenlightuser"
    password = "mysupersecretpassword"
}
```

# License

This project is released under the MIT license (http://opensource.org/licenses/MIT):

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.






