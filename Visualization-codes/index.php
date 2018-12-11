<?php  

//index.php

include("database_connection.php");

$query = "SELECT room FROM noisetable GROUP BY room DESC";

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
                            <h3 class="panel-title">Noise level in each room</h3>
                        </div>
                        <div class="col-md-3">
                            <select name="room" class="form-control" id="room">
                                <option value="">Select Zone</option>
                            <?php
                            foreach($result as $row)  
                            {
                                echo '<option value="'.$row["room"].'">'.$row["room"].'</option>';
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
            <?php
                echo "<center></br>";
                echo '<input type="button" style= "width:400px;height:30px" value="Click here to view individual user noise charts" onclick="window.location=\'http://localhost/dashboard/index1.php\'"/>';

            ?>
        </div>  
    </body>  
</html>
<script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
<script type="text/javascript">
google.charts.load('current', {packages: ['corechart', 'bar']});
google.charts.setOnLoadCallback();

// function numberofUsers(room){
//     var c;
//      $.ajax({
//         url:"user.php",
//         method:"POST",
//         data:room,
//         dataType:"JSON",
//         success:function(data)
//         {
//             findcvalue(data);
//         }
//     });
//      // $.each(data, function(i, data){
//      //        var c = data.c;
//      //        });
//      return '2';

// }
// function findcvalue(data){
//      var jsonData =data;
//      var cvalue;
//      $.each(jsonData,function(i,jsonData){
//          cvalue = jsonData.c;
//      })

//      return cvalue;

//  }
function load_monthwise_data(room1, title)
{
    // var num = numberofUsers(room1);
    var temp_title = title + ' '+room1+'' + '\n'+'level: 0 indicates complete silence'+'\n'+' level: 1 indicates slight noise'+'\n'+' level: 2 indicates medium noise in the room '+'\n'+' level: 3 indicates High noise in the room '+'\n'+ 'Number of active users in this zone: 2';
    $.ajax({
        url:"fetch.php",
        method:"POST",
        data:{room:room1},
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

    $('#room').change(function(){
        var room = $(this).val();
        if(room != '')
        {
            load_monthwise_data(room, 'Noise level in ');
        }
    });

});

</script>