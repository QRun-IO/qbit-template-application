# Getting Started

This template helps you create Application QBits - complete mini-applications with UI navigation.

## Quick Start

1. **Create from template:**
   ```bash
   gh repo create my-company/qbit-my-app --template QRun-IO/qbit-template-application
   ```

2. **Run customization script:**
   ```bash
   python scripts/customize_template.py --name my-app --package com.mycompany.qbits.myapp
   ```

3. **Build and test:**
   ```bash
   mvn clean test
   ```

## Key Differences from Other QBit Types

| Feature | Extension | Data | Application |
|---------|-----------|------|-------------|
| QAppSection | No | Optional | **Required** |
| Tables | 0-few | Few (reference) | Many (business) |
| Processes | Customizers | Sync | Full business logic |
| Widgets | No | No | **Yes** |

## What to Customize

1. `ExampleAppQBitConfig.java` - Add your configuration options
2. `ExampleAppQBitProducer.java` - Update QAppSection with your tables
3. `model/` - Replace with your entities
4. `processes/` - Implement your business logic
5. `widgets/` - Create your dashboard components

## Usage in Host Application

```java
new MyAppQBitProducer()
   .withConfig(new MyAppQBitConfig()
      .withBackendName("rdbms")
      .withTableNamePrefix("myapp"))
   .produce(qInstance, "my-app");
```
