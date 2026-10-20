$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
$utf8 = [Text.UTF8Encoding]::new($false)
$consent = [IO.File]::ReadAllText((Join-Path $root 'app/src/main/java/com/example/myapplication/PrivacyConsent.kt'))
$versionMatch = [regex]::Match($consent, 'const val POLICY_VERSION = (\d+)')
if (!$versionMatch.Success) { throw 'Missing consent policy version' }
$version = $versionMatch.Groups[1].Value
$languages = @(
    @{Dir='values';Id='en'},
    @{Dir='values-ru';Id='ru'},
    @{Dir='values-es';Id='es'}
)
$articles = [Collections.Generic.List[string]]::new()
foreach ($language in $languages) {
    $path = Join-Path $root "app/src/main/res/$($language.Dir)/privacy.xml"
    [xml]$document = [IO.File]::ReadAllText($path)
    $node = $document.SelectSingleNode('/resources/string[@name="privacy_policy_body"]')
    if (!$node) { throw "Missing policy: $path" }
    $plain = $node.InnerText.Replace('\n', "`n").Replace("\'", "'").Replace('\"', '"')
    if ($plain -notmatch "(?m)^(Version|Версия|Versión) $version\b") {
        throw "Policy and consent versions differ: $path"
    }
    [IO.File]::WriteAllText((Join-Path $root "docs/privacy-policy-$($language.Id).txt"), $plain + "`n", $utf8)
    $escaped = [System.Net.WebUtility]::HtmlEncode($plain)
    $articles.Add('<article id="' + $language.Id + '" lang="' + $language.Id + '">' + $escaped + '</article>')
}
$html = @'
<!doctype html>
<html lang="ru"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Space Salvage — Политика конфиденциальности / Privacy policy</title><style>body{font:16px/1.65 system-ui;max-width:850px;margin:40px auto;padding:0 20px;color:#182b3e}article{white-space:pre-line;margin:40px 0}a{color:#145c85}@media print{nav{display:none}article{break-before:page}}</style></head><body><nav><a href="#ru">Русский</a> · <a href="#en">English</a> · <a href="#es">Español</a> · <a href="mailto:alex.fitlin@gmail.com">alex.fitlin@gmail.com</a></nav>
'@
[IO.File]::WriteAllText((Join-Path $root 'docs/privacy-policy.html'), $html + "`n" + ($articles -join "`n") + "`n</body></html>`n", $utf8)
