package com.infilos.auth.token;

public enum TokenMode {
    DEFAULT,    // sign+encrypt, 32 id make 456 jwt
    ENCRYPTION, // encrypt, 32 id make 285 jwt
    SIGNATURE,  // sign, 32 id make 269 jwt
    PLAINTEXT   // neither, 32 id make 269 jwt
}
