(deftest test-crypt-aes-ok

  (let [secret
        (.getBytes "0123456789abcdef")

        file
        (get-temp-file "test" ".deed.aes")

        _
        (d/encode-to 42
                     (d/aes-output-stream file secret "AES/CBC/PKCS5Padding"))

        result
        (d/decode-from (d/aes-input-stream file secret "AES/CBC/PKCS5Padding"))

        ]

    (is (= 1 result))







    #_
    (is (= 1 file))

    #_
    (try
      (d/encode-to 1 file {:cipher-algorithm "RSA"
                           :cipher-secret (byte-array 128)})
      (catch Throwable e
        (is (= 1 e))
        )
      ))

  )
