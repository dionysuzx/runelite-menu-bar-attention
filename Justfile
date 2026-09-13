default:
    @just --list

test:
    ./gradlew test --no-daemon

install:
    ./scripts/install-app

run:
    open -a 'RuneLite Attention'

configure-jagex:
    /Applications/RuneLite.app/Contents/MacOS/RuneLite --configure
