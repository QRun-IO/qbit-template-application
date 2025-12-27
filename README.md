# QBit Template - Application

Template repository for creating **Application QBits** - complete, self-contained applications or major functional modules for the QQQ framework.

## When to Use This Template

Use this template when building:
- Complete mini-applications (WMS, OMS, CRM)
- Major functional modules with multiple tables
- QBits that require UI navigation (QAppSection)
- Self-contained features with business logic

## Quick Start

1. Click "Use this template" on GitHub
2. Clone your new repository
3. Run the customization script:
   ```bash
   python scripts/customize_template.py --name my-app --package com.mycompany.qbits.myapp
   ```
4. Implement your tables, processes, and widgets
5. Add your QAppSection for UI navigation

## Structure

```
src/main/java/com/kingsrook/qbits/example/
├── ExampleAppQBitConfig.java      # Configuration options
├── ExampleAppQBitProducer.java    # Entry point, registers QAppSection
├── model/
│   ├── ExampleEntity.java
│   └── ExampleChildEntity.java
├── processes/
│   └── ExampleProcess.java
├── widgets/
│   └── ExampleDashboardWidget.java
└── api/
    └── ExampleApiHandler.java
```

## Key Characteristics

| Feature | Description |
|---------|-------------|
| QAppSection | Required - provides UI navigation |
| Tables | Multiple interconnected business tables |
| Processes | Full CRUD with business logic |
| Widgets | Dashboard components |
| Security | Permission key support |
| Integration | Hooks for other QBits |

## Documentation

- [Getting Started](docs/00-getting-started.md)
- [Tables and Relationships](docs/01-tables-and-relationships.md)
- [Processes](docs/02-processes.md)
- [Widgets and UI](docs/03-widgets-and-ui.md)

## See Also

- [qbit-template-extension](https://github.com/QRun-IO/qbit-template-extension) - For infrastructure extensions
- [qbit-template-data](https://github.com/QRun-IO/qbit-template-data) - For reference data providers
