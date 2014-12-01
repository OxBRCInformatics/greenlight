#= require angular/angular.js


app = angular.module('consentQuestionTemplate',[])

app.directive "templateQuestions" , ()->
  scope :
    consent: "="
  restrict:'E'
  template:'''
            <ul id="responseslist">
              <li ng-repeat="response in consent.responses">
                <span  class="questionTitle">{{response.question.title}}</span>
                  <select ng-model="response.answer"
                          class="response"
                  ng-options="validValue as validValue.name for validValue in response.question.validValues track by validValue.value">
                  <option value="">--select--</option>
                  </select>
              </li>
            </ul>
            {{consent.responses | json}}
          '''
