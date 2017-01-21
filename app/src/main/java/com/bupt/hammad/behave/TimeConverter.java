package com.bupt.hammad.behave;

// This class converts the time from milliseconds format to 00:00:00 format
class TimeConverter {

    // This method takes the drive time parameter in milliseconds and returns time in 00:00:00 format
    public static String formatLongToString(Long driveTime) {

        int hour = 0;
        int minute = 0;
        int second;

        // divide time by 1000 and convert answer to integer for saving in second integer
        second = (int) (driveTime / 1000);

        // check if second is greater than sixty
        // if greater than sixty then minutes will be equal to seconds divided by sixty
        // and second will be the mod of seconds
        if (second > 60) {
            minute = second / 60;
            second = second % 60;
        }
        // after checking the seconds checking whether the minutes are greater than sixty or not
        // if minutes are greater than sixty than hour will be equal to minutes divided by sixty
        // and minutes will be the mod of minute
        if (minute > 60) {
            hour = minute / 60;
            minute = minute % 60;
        }
        // return the time in 00:00:00 format by passing hours, minutes and seconds to
        // zeroTimeFormatEnforcer method
        return (zeroTimeFormatEnforcer(hour) + ":" + zeroTimeFormatEnforcer(minute)  + ":"  + zeroTimeFormatEnforcer(second));
    }

    // This method takes the time in hours, minutes and seconds and adds zero to numbers
    // less than ten and returns the result
    private static String zeroTimeFormatEnforcer(final int data) {
        if(data < 10) {
            return "0" + data;
        } else {
            return "" + data;
        }
    }
}

