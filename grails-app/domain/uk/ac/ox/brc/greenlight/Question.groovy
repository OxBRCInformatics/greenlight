package uk.ac.ox.brc.greenlight

class Question {

    String name

    static auditable = true
    static belongsTo = [
            studyForm:ConsentFormTemplate]

    static constraints = {
        name maxSize: 500
    }

//    static mapping = {
//        name type: 'text'
//    }

}
