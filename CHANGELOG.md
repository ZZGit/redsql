# Change Log
All notable changes to this project will be documented in this file. This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## [Unreleased]
### Changed
- Add a new arity to `make-widget-async` to provide a different widget shape.

## [0.1.1] - 2021-04-10
### Changed
- Documentation on how to make the widgets.

### Removed
- `make-widget-sync` - we're all async, all the time.

### Fixed
- Fixed widget maker to keep working when daylight savings switches over.

## 0.1.0 - 2021-04-10
### Added
- Files from the new template.
- Widget maker public API - `make-widget-sync`.

## 0.1.3-SNAPSHOT
### Added
- 添加`redsql.helper/convert-rows`方法,方便转换分页中rows数据
- 添加`(:refer-clojure :exclude [set group-by])`, 防止启动警告

[Unreleased]: https://github.com/your-name/red-db/compare/0.1.1...HEAD
[0.1.1]: https://github.com/your-name/red-db/compare/0.1.0...0.1.1
