package uk.ac.ox.brc.greenlight

class StudyForm {

    String name
    String version
    Date validFrom
    Date validTo

    static hasMany = [stuyFormQuestions:StudyFormQuestion]
    static constraints = {
    }
}