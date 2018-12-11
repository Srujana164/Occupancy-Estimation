<?php  

//index.php

include("database_connection.php");

$query = "SELECT email FROM noisetable GROUP BY email DESC";

$statement = $connect->prepare($query);

$statement->execute();

$result = $statement->fetchAll();

?>  
<!DOCTYPE html>  
<html>  
    <head>  
        <title>Visualization of Noise Data</title>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        <script src="https://code.jquery.com/jquery-1.12.4.js"></script> 
    </head>  
    <body> 
        <br /><br />
        <div class="container">  
            <h3 align="center">Visualization of Noise Data</h3>  
            <br />  
            
            <div class="panel panel-default">
                <div class="panel-heading">
                    <div class="row">
                        <div class="col-md-9">
                            <h3 class="panel-title">Noise level for each user</h3>
                        </div>
                        <div class="col-md-3">
                            <select name="email" class="form-control" id="email">
                                <option value="">Select User</option>
                            <?php
                            foreach($result as $row)  
                            {
                                echo '<option value="'.$row["email"].'">'.$row["email"].'</option>';
                            }
                            ?>
                            </select>
                        </div>
                    </div>
                </div>
                <div class="panel-body">
                    <div id="chart_area" style="width: 1000px; height: 620px;"></div>
                </div>
            </div>
        </div>  
    </body>  
</html>
<script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
<script type="text/javascript">
google.charts.load('current', {packages: ['corechart', 'bar']});
google.charts.setOnLoadCallback();


function load_monthwise_data(email, title)
{
    var temp_title = title + ' '+email+'' + '\n'+'level: 0 indicates complete silence'+'\n'+' level: 1 indicates slight noise'+'\n'+' level: 2 indicates medium noise in the email '+'\n'+' level: 3 indicates High noise in the email ';
    $.ajax({
        url:"fetch1.php",
        method:"POST",
        data:{email:email},
        dataType:"JSON",
        success:function(data)
        {
            drawMonthwiseChart(data, temp_title);
        }
    });
}

function drawMonthwiseChart(chart_data, chart_main_title)
{
    var jsonData = chart_data;
    var data = new google.visualization.DataTable();
    data.addColumn('string', 'TimeValue');
    data.addColumn('number', 'level');
    $.each(jsonData, function(i, jsonData){
       var TimeValue = jsonData.TimeValue;
        var level = parseInt(jsonData.level);
        data.addRows([[TimeValue, level]]);
    });
    var options = {
        title:chart_main_title,
        hAxis: {
            title: "Time"
        },
        vAxis: {
            title: 'noiselevel'
        }
    };

    var chart = new google.visualization.ColumnChart(document.getElementById('chart_area'));
    chart.draw(data, options);
}
</script>

<script>
    
$(document).ready(function(){

    $('#email').change(function(){
        var email = $(this).val();
        if(email != '')
        {
            load_monthwise_data(email, 'Noise level for ');
        }
    });

});

</script>
