<?php

//$connection = mysqli_connect("localhost", "root", "", "lockmate");
$connection = mysqli_connect("localhost", "arbna", "a13cs0175", "db_arbna");

$query = "SELECT * FROM qr_keys";

$result = mysqli_query($connection, $query);

while ($row = mysqli_fetch_assoc($result)) {
    $array[] = $row;
}

header('Content-Type: application/json');

echo json_encode($array);

?>

