Greenlight 
=======
[![Build Status](https://travis-ci.org/OxBRCInformatics/greenlight.png?branch=develop)](https://travis-ci.org/OxBRCInformatics/greenlight)

Open Source consent management tool
------------
This project is built in Grails and uses GSP in most sections.
A number of libraries such as JQuery, Bootstrap,... are also used.



## Features
Main features of this open source project are as the followings:
* [Uploading scanned forms in all image formats and PDF format](#upload)
* [Supporting multi-page PDF consent forms](#multipagePDF)
* [Supporting several consent forms](#severalforms)
* [Annotating uploaded consent forms](#annotate)
* [Cut-Up room consent tracker](#cutup) 
* [Supporting search on consent forms content](#search)
* [Supports IE7, IE8 and higher versions](#IE)
* ...


#### <a name="upload" style="text-decoration:none">Uploading scanned forms in all image formats and PDF format]</a>
Most image files and pdf format are supported in this online system.
<p align="center">
    <img width="400" height="200" src="/resources/docImages/uploadedFiles.png"/>
</p>

#### <a name="annotate" style="text-decoration:none">Supporting multi-page PDF consent forms</a>
Scanning several consent forms as batch may result to a multi-page PDF file in which each consent form is saved in a page. In this case the file can be uploaded and 'Consent Management System' will save each page as a separate consent form.

#### <a name="severalforms" style="text-decoration:none">Supporting several consent form templates</a>
In ORB, several consent forms are used such as ORB General Consent Form, Oncology Consent Form for Adults and .... . In this case users can select the consent form type while annotating the scanned form.
<p align="center">
    <img width="500" height="300" src="/resources/docImages/consentForms.png"/>
</p>

#### <a name="annotate" style="text-decoration:none">Annotating uploaded consent forms</a>
By annotating a consent form, an operator will enter all details os a consent form such as NHS number, MRN, consent date, consent take name,answers to each consent questions,...
This process is facilitated by a number of fast entry buttons such as Answer 'Yes to all', Answer 'No to all',...
In case of ambiguous or blank answers to a consent question, operators can easily select an item form the list and apply this option for a question.
<p align="center">
    <img width="500" height="300" src="/resources/docImages/annotation.png"/>
</p>

#### <a name="cutup" style="text-decoration:none">Cut-Up room consent tracker</a>
This feature enables users such as pathologists and researchers at cut-up rooms to use Barcode readers to read barcodes on samples and check wheather the sample is consented or not. In this features, the read code is checked agains NHS number and MRN to find the related information.
<p align="center">
    <img width="500" height="300" src="/resources/docImages/cutuproom.png"/>
    <img width="500" height="300" src="/resources/docImages/cutuproom-consented.png"/>
    <img width="500" height="300" src="/resources/docImages/cutuproom-Notconsented.png"/>
    
    
</p>


#### <a name="search" style="text-decoration:none">Supporting search on consent forms details</a>
Users can search for consent forms based on NHS Number, MRN, Consent date, Consent Taker name and ...
<p align="center">
    <img width="500" height="300" src="/resources/docImages/search.png"/>
</p>


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







