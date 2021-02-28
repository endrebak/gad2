# gad2

FIXME: description

## Installation

Download from http://example.com/FIXME.

## Usage

FIXME: explanation

    $ java -jar gad2-0.1.0-standalone.jar [args]

## Options

FIXME: listing of options this app accepts.

## Examples

...

### Bugs

...

### Any Other Sections
### That You Think
### Might be Useful

## License

Copyright © 2020, 2021 FIXME

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.

## How to

```
(require 'gad2.core)
(require 'gad2.jobgraph)

(def jobgraph (gad2.core/jobgraph-from-config "examples/snakemake/config.edn"))

(def rules (gad2.parse-rulefiles/read-all "examples/snakemake/rules.clj"))

(def deps (:dependents jobgraph))

(let [rule (-> deps first first)
  rulename (-> rule first)
  wildcards (-> rule second sort)] [rulename wildcards])

;; must apply over whole file
(let [rule (-> deps first first)
  rulename (-> rule first name)
  wildcards (->> rule second sort flatten (map name))
  parts (flatten [rulename wildcards])] (str/join "/" parts))
```

## What remains?

Convert rules into code to execute.

- Instantiate rules
  - find path of input-files: given by wildcards and rule name (hash of code and input-files also?)
  - find path of output-files: given by wildcards and rule name (hash of code and input-files also?)

go through each (:dependents jobgraph). Then pick out :keyword-keys and you have the rule-name and the name of the file with the wildcards.
