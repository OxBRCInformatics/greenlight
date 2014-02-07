package uk.ac.ox.brc.greenlight

class Response {

    Question question
    ResponseValue answer
    String description

    static auditable = true
    static belongsTo = [
            consentForm: ConsentForm
    ]
    static constraints = {
        description nullable: true
    }


    enum ResponseValue {
        YES("Yes"), NO("No"),BLANK("Blank"),OTHER("Other");
        final String value;
        ResponseValue(String value) { this.value = value; }
        String toString() { value; }
        String getKey() { name(); }
    }
}