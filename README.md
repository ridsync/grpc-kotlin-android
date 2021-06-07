
# gRPC Kotlin Android Example


## OverView

안드로이드에서 gRPC FrameWork를 사용하기 위한 개발 환경설정 예재 프로젝트.


## gRPC Gradle Config - generated Kotlin grpc

- build.gradle (Project)
```groovy
buildscript {
	dependencies {
		classpath "com.google.protobuf:protobuf-gradle-plugin:0.8.5"
	}
}

```

- build.gradle (App)

```groovy
plugins {
    id "com.google.protobuf"
}

sourceSets{
    main.java.srcDirs += 'src/main/kotlin'
    test.java.srcDirs += 'src/test/kotlin'
    androidTest.java.srcDirs += 'src/androidTest/kotlin'
    main.proto.srcDirs += 'src/main/proto'
}

protobuf {
    // The normal gRPC configuration for Android goes here
    protoc { artifact = 'com.google.protobuf:protoc:3.12.0' }
    plugins {
        grpc { artifact = 'io.grpc:protoc-gen-grpc-java:1.37.0' }
        grpckt { artifact = 'io.grpc:protoc-gen-grpc-kotlin:1.0.0:jdk7@jar' }
    }
    generateProtoTasks {
        all().each { task ->
            task.plugins {
                java { option 'lite' }
                grpc { option 'lite' }
                grpckt { option 'lite' }
            }
        }
    }
}

dependencies {
		// gRPC
		def grpc_version = "1.37.0"
    implementation 'javax.annotation:javax.annotation-api:1.3.2'
    implementation "io.grpc:grpc-protobuf-lite:$grpc_version"
    implementation "io.grpc:grpc-kotlin-stub:1.0.0"
    implementation "io.grpc:grpc-okhttp:$grpc_version"
    configurations {
        implementation.exclude module:'protobuf-java' // Conflict class with firebase
    }
}
```

- proguard-rules.pro

```groovy
#gRPC
~~-keep class kr.co.april7.dtk2.grpc.* { *; }~~
-dontwarn sun.misc.Unsafe
-dontwarn com.google.common.**
-dontwarn javax.naming.**
-dontwarn okio.**
# Ignores: can't find referenced class javax.lang.model.element.Modifier
-dontwarn com.google.errorprone.annotations.**
```

- 컴파일할 Proto File 정의 파일들을 `main.proto.srcDirs`에서 지정한 디렉토리에 저장한다.


- Android Studio(IDE)에서 빌드하면 gRPC 클래스가 지정된 Path에 자동 생성된다 
- app/build/source/proto/{package or java path option in protofile}
Generated Codes
  1. `gRPC Service`
  2. `Channel - AndroidManagedChannel`
  3. `Stub`

---

- gRPC in Repository Pattern

```kotlin

TBD

```

---



## Documents

- [https://grpc.io/blog/kotlin-gradle-projects/](https://grpc.io/blog/kotlin-gradle-projects/)
- [https://github.com/grpc/grpc-kotlin](https://github.com/grpc/grpc-kotlin)
- [https://github.com/grpc/grpc-java](https://github.com/grpc/grpc-java)
- [https://github.com/grpc/grpc-java/blob/v1.29.0/examples/example-kotlin/android](https://github.com/grpc/grpc-java/blob/v1.29.0/examples/example-kotlin/android)
- [https://github.com/grpc/grpc-kotlin/tree/master/examples/android](https://github.com/grpc/grpc-kotlin/tree/master/examples/android)
- [https://github.com/google/protobuf-gradle-plugin](https://github.com/google/protobuf-gradle-plugin)
