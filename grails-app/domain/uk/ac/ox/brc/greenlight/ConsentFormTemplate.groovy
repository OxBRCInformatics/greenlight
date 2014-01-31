package uk.ac.ox.brc.greenlight

class ConsentFormTemplate {

    String name
    String version

    static hasMany = [
            questions:Question
    ]
    static constraints = {

    }
}