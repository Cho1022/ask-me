$ErrorActionPreference = 'Stop'

$backendPath = (Resolve-Path (Join-Path $PSScriptRoot '..\backend')).Path
$driveLetter = @('R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z') |
    Where-Object { -not (Test-Path "${_}:\") } |
    Select-Object -First 1

if (-not $driveLetter) {
    throw '백엔드 테스트에 사용할 빈 임시 드라이브 문자가 없습니다.'
}

$drive = "${driveLetter}:"
$exitCode = 1
$locationPushed = $false

try {
    & subst.exe $drive $backendPath
    Push-Location "${drive}\"
    $locationPushed = $true
    & .\gradlew.bat test --no-daemon
    $exitCode = $LASTEXITCODE
} finally {
    if ($locationPushed) {
        Pop-Location
    }
    & subst.exe $drive /D
}

exit $exitCode
