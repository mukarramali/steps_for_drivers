<?php
  require "config.php";

  $query = "select * from ".VEHICLE_TABLE;
  //Execute query
  $qry_result = mysqli_query($conn, $query) or failed();
  // Insert a new row in the table for each person returned
  if ($qry_result->num_rows > 0) {
      $rows = array();
      $i = 0;
      while($row = $qry_result->fetch_assoc()) {
        $rows[$i] = $row;
        $i += 1;
      }
      $response_data = json_encode($rows);
  } else {
      $response_data = "[]";
  }

  echo "{\"response_data\":".$response_data.", \"status\": true}";

?>