# nfc-signer-lib
1. Include library to project:

```gradle
repositories {
    maven {
        url 'https://dl.bintray.com/jer1ch0/maven'
    }
}
dependencies {
    compile 'com.signature.nfc.nfc_sign:nfc-signer:1.0.1'
}
```

2. Start reading card from your activity:

```java
Intent intent = new Intent(MainActivity.this, RsaApduActivity.class);
Bundle bundle = new Bundle();
bundle.putString("message", /*YOUR DATA*/);
bundle.putString("password", /*PASSWORD*/);
bundle.putString("key", /*SIGNATURE KEY ID*/);
intent.putExtras(bundle);
startActivityForResult( intent, READ_REQUEST_CODE);
```

3. Handle callback with sign results:
```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
  if (requestCode == READ_REQUEST_CODE) {
    if(resultCode == RESULT_OK) {
       //handle data.getStringExtra("result")
    }
    if(resultCode == RESULT_CANCELED) {
       //handle data.getStringExtra("error")
    }
  }
}
```

Simple example: https://github.com/jer1ch0/nfc-signer-demo
