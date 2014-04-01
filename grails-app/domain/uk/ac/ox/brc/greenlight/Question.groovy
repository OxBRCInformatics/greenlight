package uk.ac.ox.brc.greenlight

class Question {

    String name
	Boolean optional = false

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
