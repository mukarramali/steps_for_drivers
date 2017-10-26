<?php
require "config.php";

function create($array){
  return "insert into ".VEHICLE_TABLE."(".VEHICLE_ID.", ".PHONE.", ".LATITUDE.", ".LONGITUDE.", ".BEARINGS.") values(".$array[VEHICLE_ID].", ".$array[PHONE].", ".$array[LATITUDE].", ".$array[LONGITUDE].", ".$array[BEARINGS].")";
}

function update($array){
  return "update ".VEHICLE_TABLE." set ".PHONE."=".$array[PHONE].", ".LATITUDE."=".$array[LATITUDE].", ".LONGITUDE."=".$array[LONGITUDE].", ".BEARINGS."=".$array[BEARINGS]." where ".VEHICLE_ID."=".$array[VEHICLE_ID];
}


if(isset($_POST[VEHICLE_ID]) && isset($_POST[PHONE]) && isset($_POST[LATITUDE]) && isset($_POST[LONGITUDE]) && isset($_POST[BEARINGS]))
{
  $query = "select * from ".VEHICLE_TABLE." where ".VEHICLE_ID."=".$_POST[VEHICLE_ID];
  $qry_result = mysqli_query($conn, $query) or failed();
  if(mysqli_fetch_array($qry_result) == 0)
    $query = create($_POST);
  else
    $query = update($_POST);
  if(mysqli_query($conn, $query))
    echo "{\"status\": true}";
  else
    failed();

}
else failed();

?>