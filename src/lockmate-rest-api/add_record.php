<?php

//$connection = mysqli_connect("localhost", "root", "", "lockmate");
$connection = mysqli_connect("localhost", "arbna", "a13cs0175", "db_arbna");


if (isset($_POST["qr_key_id"])) {
    $qr_key_id = $_POST["qr_key_id"];
    header('Content-Type: application/json');

    if (!empty($qr_key_id)) {
        $query = "INSERT INTO access_log(qr_key_id, scan_datetime) VALUES('$qr_key_id', now() )";

        $result = mysqli_query($connection, $query);

        // check if row inserted or not
        if ($result) {
            // successfully inserted into database
            echo "Key successfully created.";
        }
        else {
            // failed to insert row
            echo "Oops! An error occured. \n";
            echo "Error: " . mysqli_error($connection);
        }
    }
    else {
        echo "You must fill qr_key_id field";
    }
}
?>