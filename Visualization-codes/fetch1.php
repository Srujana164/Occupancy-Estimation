<?php

//fetch.php

include('database_connection.php');

if(isset($_POST["email"]))
{
 $query = "
 SELECT TimeValue,level FROM noisetable
 WHERE email = '".$_POST["email"]."' 
 ORDER BY id ASC
 ";
 $statement = $connect->prepare($query);
 $statement->execute();
 $result = $statement->fetchAll();
 foreach($result as $row)
 {
  $output[] = array(
   'TimeValue'   => $row["TimeValue"],
   'level'  =>(int)$row["level"]
  );
 }
 
 echo json_encode($output);
}

?>