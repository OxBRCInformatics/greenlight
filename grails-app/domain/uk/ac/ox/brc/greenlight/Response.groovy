package uk.ac.ox.brc.greenlight

class Response {


    Question question

    ResponseValue answer
    String description

    static belongsTo = [
            consentForm: ConsentForm
    ]
    static constraints = {
    }


    enum ResponseValue{
        YES,
        NO,
        BLANK,
        OTHER
    }
}
