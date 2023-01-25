package roteUtils;

import java.util.UUID;

public class UuidHelper {
    public static String GetNewUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
