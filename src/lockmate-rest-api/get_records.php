<?php
/**
 * Created by PhpStorm.
 * User: T430
 * Date: 15/5/2017
 * Time: 11:18 PM
 */

//$connection = mysqli_connect("localhost", "root", "", "lockmate");
$connection = mysqli_connect("localhost", "arbna", "a13cs0175", "db_arbna");

$query =
    "SELECT qr_keys.studentName, qr_keys.matricNo, access_log.scan_datetime
    FROM `access_log` INNER JOIN qr_keys
    ON access_log.qr_key_id = qr_keys.id";

$result = mysqli_query($connection, $query);

while ($row = mysqli_fetch_assoc($result)) {
    $array[] = $row;
}

header('Content-Type: application/json');

echo json_encode($array);
