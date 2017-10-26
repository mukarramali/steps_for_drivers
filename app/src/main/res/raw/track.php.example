<?php
  require "config.php";

  if(isset($_GET[VEHICLE_ID]))
  {
    $vehicle_id = $_GET[VEHICLE_ID];
    $query = "select * from ".VEHICLE_TABLE." where ".VEHICLE_ID."=".$vehicle_id;
    //Execute query
    $qry_result = mysqli_query($conn, $query) or failed();
    $rows = mysqli_fetch_assoc($qry_result);
    $response_data = json_encode($rows);
    echo "{\"response_data\":".$response_data.", \"status\": true}";
  }
  else failed();

?>
