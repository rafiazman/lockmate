<?php

//$connection = mysqli_connect("localhost", "root", "", "lockmate");
$connection = mysqli_connect("localhost", "arbna", "a13cs0175", "db_arbna");


if (isset($_POST["id"], $_POST["studentName"], $_POST["matricNo"], $_POST["password"])) {
    $id = $_POST["id"];
    $studentName = $_POST["studentName"];
    $matricNo = $_POST["matricNo"];
    $password = $_POST["password"];
    header('Content-Type: application/json');

    if (!empty($id) && !empty($studentName) && !empty($matricNo) && !empty($password)) {
        $query = "UPDATE qr_keys SET studentName='" . $studentName . "', matricNo='" . $matricNo . "', password='" . $password . "' WHERE id='" . $id . "';";

        $result = mysqli_query($connection, $query);

        // check if row inserted or not
        if ($result) {
            // successfully inserted into database
            echo "Key successfully updated.";
        }
        else {
            // failed to insert row
            echo "Oops! An error occured.";
        }

    }
    else {
        echo "You must fill all fields (id, studentName, matricNo & password)";
    }
}
?>