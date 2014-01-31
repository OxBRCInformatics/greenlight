<%@ page import="uk.ac.ox.brc.greenlight.Response" %>
<ul class="list-group" id="templateQuestions">
    <g:hiddenField name="questionsSize" value="${questions?.size()}"/>


    <g:if test="${questions && questions.size()>0}">
        <li class="list-group-item">

            <button type="button" class="btn btn-success btn-sm" onclick="$('select[id^=answers]').val('YES')">All 'yes'</button>
            <button type="button" class="btn btn-danger btn-sm" onclick="$('select[id^=answers]').val('NO')">All 'no'</button>
            <button type="button" class="btn btn-primary btn-sm" onclick="$('select[id^=answers]').val('BLANK')">All 'blank'</button>

        </li>
    </g:if>
    <g:each in="${questions}" var="question" status="index">

        <li class="list-group-item">
            <span class="label label-primary bootstrapTooltip" style="margin-right: 3px;clear:both;cursor: pointer;"
                  data-toggle="tooltip" data-placement="right" data-html="true" title="<div style='text-align:left'>${question?.name}</div>">${index+1}</span>
             %{--<span style="font-size: 10px;">${question.name.substring(0,Math.min(10,question?.name.size()))+ "..."}</span>--}%


            <g:select class="form-control" id="answers.${index}"
                      name="responses.${index}"
                      value="${ responses ? responses[index]?.answer.key : null}"
                      from="${Response.ResponseValue?.values()}"
                      optionKey="key"
                      optionValue="value"
                      style="margin-left:5px;width: 80%; display: inline !important"
                       />
            <span class="glyphicon glyphicon-question-sign bootstrapTooltip"
                  data-toggle="tooltip" data-placement="left" data-html="true" title="<div style='text-align:left'>${question?.name}</div>"
                  style=";cursor: pointer;display: inline !important"></span>

        </li>

    </g:each>
</ul>

