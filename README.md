Greenlight 
=======
[![Build Status](https://travis-ci.org/OxBRCInformatics/greenlight.png?branch=develop)](https://travis-ci.org/OxBRCInformatics/greenlight)

Open Source consent management tool
------------
This project is built in Grails and uses GSP in most sections.
A number of libraries such as JQuery, Bootstrap,... are also used.

## Documentation
Please see [the wiki](https://github.com/OxBRCInformatics/greenlight/wiki) for documentation and deployment instructions.

## Features
Main features are as the followings:
* [Uploading scanned forms in all image formats and PDF format](#upload)
* [Supporting multi-page PDF consent forms](#multipagePDF)
* [Supporting several consent forms](#severalforms)
* [Annotating uploaded consent forms](#annotate)
* [Cut-Up room consent tracker](#cutup) 
* [Supporting search on consent forms content](#search)
* [Supports IE7, IE8 and higher versions](#IE)
* [REST API Endpoint](#REST)
* ...


#### <a name="upload" style="text-decoration:none">Uploading scanned forms in all image formats and PDF format</a>
Most image files and pdf format are all supported.

![Uploaded files](/resources/docImages/uploadedFiles.png)

#### <a name="annotate" style="text-decoration:none">Supporting multi-page PDF consent forms</a>
Scanning several consent forms as a batch may result in a multi-page PDF file in which each consent form is saved in a page. In this case, the file can be uploaded and 'Consent Management System' will save each page as a separate consent form and operators will annotate each page seperately .

![Multi page PDF](/resources/docImages/multipagePDF.png)

#### <a name="severalforms" style="text-decoration:none">Supporting several consent form templates</a>
In ORB, several consent forms are used such as General Consent Form, Oncology Consent Form for Adults and .... . In this case users can select the consent form type while annotating the scanned form.

![Consent forms](/resources/docImages/consentForms.png)

#### <a name="annotate" style="text-decoration:none">Annotating uploaded consent forms</a>
By annotating a consent form, an operator will enter all details of a consent form such as NHS number, MRN, consent date, consent take name,answers to each consent questions,...
This process is facilitated by a number of fast entry buttons such as Answer 'Yes to all', Answer 'No to all',...
In case of ambiguous or blank answers to a consent question, operators can easily select an item form the list and apply this option for a question.
![Annotations](/resources/docImages/annotation.png)

#### <a name="cutup" style="text-decoration:none">Cut-Up room consent tracker</a>
This feature enables users such as pathologists and researchers at cut-up rooms to use Barcode readers to read barcodes on samples and check wheather the sample is consented or not. In this features, the read code is checked agains NHS number and MRN to find the related information.
![Cut up room](/resources/docImages/cutuproom.png)
![Cut up room - consented](/resources/docImages/cutuproom-consented.png)
![Cut up room - not consented](/resources/docImages/cutuproom-Notconsented.png)
![Cut up room - not found](/resources/docImages/cutuproom-Notfound.png)

#### <a name="search" style="text-decoration:none">Supporting search on consent forms content</a>
Users can search for consent forms based on NHS Number, MRN, Consent date, Consent Taker name and ...
![Search](/resources/docImages/search.png)



#### <a name="REST" style="text-decoration:none">REST API Endpoints</a>
This feature enables external client systems to query for consents and retrived details in JSON format.
This is a fully secured token-based REST API endpoint which is designed based on Spring Security. The followings are the main endpoints:

API          | Description              | Input                                         | Output
---          | ---                      | ---                                           | ---
`/api/login` | login & generates token  |   *username,password as JSON body*    | token value & user details
`/api/validate` | checks if a token is valid| *token as HTTP header('Authentication') parameter* | token value & user details
`/api/logout`   | logout    | *token as HTTP header('Authentication') parameter*  | HTTP 200 (empty body)
`/api/consents/` | returns consent details for a patient    | *token as HTTP header('Authentication') parameter* + nhsNumber as query string like /api/consents/SAMPLE_NHS_NUMBER.json | aptient consents


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
You also need to set database connection for demographic database where Greenlight retrives patient demographic data from.
This setting should be specified in 'greenlight-config.groovy' file like the following:
```
epds.conString.username = "USERNAME"
epds.conString.password = "PASSWORD"
epds.conString.url='jdbc:oracle:thin:@serverName:1521:SIDName'
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







