<?php

$username = $_POST['room'];
$useremail = $_POST['TimeValue'];
$userpassword = $_POST['noiselevel'];
$email = $_POST['email'];
$level = $_POST['level'];
$user = "root";
$pass = "";
$host= "localhost";
$dbname="project";

$con = mysqli_connect($host,$user,$pass,$dbname);
$sql="insert into noisetable(room,TimeValue,noiselevel,email,level) values('".$username."','".$useremail."','".$userpassword."','".$email."','".$level."');";
if(mysqli_query($con,$sql)){
	echo  "data inserted";
	
}else{	
	echo "Failed";
}
?>