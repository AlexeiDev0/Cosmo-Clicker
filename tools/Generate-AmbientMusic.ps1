$ErrorActionPreference='Stop'
$root=Split-Path -Parent $PSScriptRoot
Add-Type -TypeDefinition @'
using System;
using System.IO;
public static class OrbitAmbient {
 public static void Write(string path, int variant) {
  const int rate=22050, seconds=72;
  int count=rate*seconds;
  int[][] chords={new[]{45,52,57,60},new[]{41,48,53,57},new[]{48,55,60,64},new[]{43,50,55,59}};
  using(var w=new BinaryWriter(File.Create(path))) {
   w.Write(System.Text.Encoding.ASCII.GetBytes("RIFF"));w.Write(36+count*4);
   w.Write(System.Text.Encoding.ASCII.GetBytes("WAVEfmt "));w.Write(16);w.Write((short)1);w.Write((short)2);
   w.Write(rate);w.Write(rate*4);w.Write((short)4);w.Write((short)16);
   w.Write(System.Text.Encoding.ASCII.GetBytes("data"));w.Write(count*4);
   for(int i=0;i<count;i++) {
    double t=(double)i/rate, left=0,right=0;
    for(int c=0;c<6;c++) {
     double start=c*12, local=t-start;
     if(local<0||local>18)continue;
     double env=Math.Min(1,local/3)*Math.Min(1,(18-local)/6);
     int[] chord=chords[(c+variant)%4];
     for(int n=0;n<4;n++) {
      double f=440*Math.Pow(2,(chord[n]+variant*2-69)/12.0);
      double a=.055*env*(1+.15*Math.Sin(t*.32+n));
      left+=a*(Math.Sin(2*Math.PI*f*t)+.22*Math.Sin(2*Math.PI*f*2*t));
      right+=a*(Math.Sin(2*Math.PI*f*1.001*t+.15)+.22*Math.Sin(2*Math.PI*f*2.001*t));
     }
    }
    double beat=t%1.5, noteEnv=Math.Exp(-beat*3)*Math.Min(1,beat/.025);
    int note=new[]{69,72,76,79,76,72,67,72}[(int)(t/1.5)%8]+variant*2;
    double freq=440*Math.Pow(2,(note-69)/12.0);
    left+=.028*noteEnv*Math.Sin(2*Math.PI*freq*t);
    right+=.023*noteEnv*Math.Sin(2*Math.PI*freq*t+.2);
    double fade=Math.Min(1,t/2)*Math.Min(1,(seconds-t)/3);
    w.Write((short)(Math.Tanh(left)*fade*25000));w.Write((short)(Math.Tanh(right)*fade*25000));
   }
  }
 }
}
'@
0..2 | ForEach-Object { [OrbitAmbient]::Write((Join-Path $root ('app/src/main/res/raw/music_orbit_ambient_'+($_+1)+'.wav')), $_) }
