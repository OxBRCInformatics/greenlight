package uk.ac.ox.brc.greenlight

class Question {

    String name

    static belongsTo = [
            studyForm:ConsentFormTemplate]

    static constraints = {
    }

    enum QuestionType {
        UNKOWN("Unknown"), YES_NO("Yes No"),DESCRIPTION("Description");
        final String value;
        QuestionType(String value) { this.value = value; }
        String toString() { value; }
        String getKey() { name(); }
    }
}
