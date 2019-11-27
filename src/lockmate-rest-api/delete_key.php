<?php

//$connection = mysqli_connect("localhost", "root", "", "lockmate");
$connection = mysqli_connect("localhost", "arbna", "a13cs0175", "db_arbna");


if (isset($_POST["id"])) {

    $id = $_POST["id"];
    header('Content-Type: application/json');

    if (!empty($id)) {
        $query = "DELETE FROM qr_keys WHERE id = '$id'";

        $result = mysqli_query($connection, $query);

        // check if row deleted or not
        if ($result) {
            // successfully inserted into database
            echo "Key successfully deleted.";
        }
        else {
            // failed to delete row
            echo "Oops! An error occured. Your input: ".$id;
        }

    }
    else {
        echo "Error: missing id";
    }
}
?>