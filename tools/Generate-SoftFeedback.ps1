$ErrorActionPreference='Stop'
$root=Split-Path -Parent $PSScriptRoot
function Write-Feedback([string]$path,[double]$duration,[bool]$collect) {
 $rate=44100
 $samples=[int]($rate*$duration)
 $stream=[IO.File]::Create($path)
 $writer=[IO.BinaryWriter]::new($stream)
 try {
  $writer.Write([Text.Encoding]::ASCII.GetBytes('RIFF')); $writer.Write([int](36+$samples*2))
  $writer.Write([Text.Encoding]::ASCII.GetBytes('WAVEfmt ')); $writer.Write([int]16)
  $writer.Write([int16]1); $writer.Write([int16]1); $writer.Write([int]$rate)
  $writer.Write([int]($rate*2)); $writer.Write([int16]2); $writer.Write([int16]16)
  $writer.Write([Text.Encoding]::ASCII.GetBytes('data')); $writer.Write([int]($samples*2))
  for($i=0;$i -lt $samples;$i++) {
   $t=$i/$rate
   $attack=[Math]::Min(1,$t/.004)
   $fade=[Math]::Min(1,($duration-$t)/.018)
   $envelope=$attack*$fade*[Math]::Exp(-$t/$(if($collect){.045}else{.017}))
   if($collect) { $wave=.55*[Math]::Sin(2*[Math]::PI*660*$t)+.25*[Math]::Sin(2*[Math]::PI*990*$t)+.1*[Math]::Sin(2*[Math]::PI*1320*$t) }
   else { $wave=.65*[Math]::Sin(2*[Math]::PI*(430*$t-1200*$t*$t))+.18*[Math]::Sin(2*[Math]::PI*860*$t) }
   $writer.Write([int16]([Math]::Round(18000*$wave*$envelope)))
  }
 } finally { $writer.Dispose() }
}
Write-Feedback (Join-Path $root 'app/src/main/res/raw/sfx_click_soft.wav') .065 $false
Write-Feedback (Join-Path $root 'app/src/main/res/raw/sfx_collect_soft.wav') .15 $true
